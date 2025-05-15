package com.example.Foodie.controller;

import com.example.Foodie.dto.AddressDto;
import com.example.Foodie.dto.UserProfileDataDto;
import com.example.Foodie.model.User;
import com.example.Foodie.model.UserProfile;
import com.example.Foodie.service.UserProfileService;
import com.example.Foodie.service.UserService;
import com.example.Foodie.service.storage.FileStorageService;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource; // For streaming
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService,
                                 UserService userService,
                                 FileStorageService fileStorageService) {
        this.userProfileService = userProfileService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    // Display current user's profile
    @GetMapping
    public String viewProfile(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        User user = userService.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // Use getOrCreate instead of getUserProfile to handle missing profiles
        UserProfile userProfile = userProfileService.getOrCreateUserProfile(user);

        model.addAttribute("userProfile", userProfile);
        model.addAttribute("user", user);
        return "profile/profile-view";
    }


    @GetMapping("/edit")
    public String showEditProfileForm(Model model,
                                      @AuthenticationPrincipal UserDetails currentUserDetails) {
        User user = userService.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile userProfile = userProfileService.getOrCreateUserProfile(user);

        UserProfileDataDto profileDto = new UserProfileDataDto();
        profileDto.setFirstName(userProfile.getFirstName());
        profileDto.setLastName(userProfile.getLastName());

        if (userProfile.getAddress() != null) {
            profileDto.setAddress(new AddressDto(
                    userProfile.getAddress().getStreetAddress(),
                    userProfile.getAddress().getSector(),
                    userProfile.getAddress().getDistrict(),
                    userProfile.getAddress().getCountry()
            ));
        } else {
            profileDto.setAddress(new AddressDto());
        }

        model.addAttribute("userProfileDto", profileDto);
        model.addAttribute("currentProfilePictureFileId", userProfile.getProfilePictureFileId());
        model.addAttribute("userId", user.getId()); // Add userId to model
        return "profile/profile-edit";
    }

    // Process profile update
    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("userProfileDto") UserProfileDataDto userProfileDto,
                                BindingResult bindingResult,
                                @RequestParam(name = "profilePictureFile", required = false) MultipartFile profilePictureFile,
                                @AuthenticationPrincipal UserDetails currentUserDetails,
                                RedirectAttributes redirectAttributes, Model model) {
        if (currentUserDetails == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            UserProfile userProfile = userProfileService.getUserProfile(
                    userService.findByUsername(currentUserDetails.getUsername()).orElseThrow()
            );
            model.addAttribute("currentProfilePictureFileId", userProfile.getProfilePictureFileId());
            return "profile/profile-edit"; // Return to form with validation errors
        }

        User user = userService.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));

        try {
            userProfileService.updateUserProfile(user, userProfileDto, profilePictureFile);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile picture: " + e.getMessage());
            // Log error: e.printStackTrace();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating profile: " + e.getMessage());
            // Log error: e.printStackTrace();
        }
        return "redirect:/profile";
    }

    // Endpoint to serve profile pictures from GridFS
    @GetMapping("/picture/{userId}")
    @ResponseBody // Indicates the return value should be directly bound to the web response body
    public ResponseEntity<InputStreamResource> getProfilePicture(@PathVariable Long userId) {
        try {
            User user = (User) userService.findById(userId) // You might need to add findById to UserService
                    .orElse(null);
            if (user == null) return ResponseEntity.notFound().build();

            UserProfile userProfile = userProfileService.getUserProfile(user);
            if (userProfile != null && userProfile.getProfilePictureFileId() != null) {
                GridFsResource resource = fileStorageService.getResource(userProfile.getProfilePictureFileId());
                if (resource != null && resource.exists()) {
                    GridFSFile gridFsFile = fileStorageService.getGridFSFile(userProfile.getProfilePictureFileId());
                    String contentType = (gridFsFile != null && gridFsFile.getMetadata() != null) ?
                            gridFsFile.getMetadata().getString("contentType") : MediaType.APPLICATION_OCTET_STREAM_VALUE;

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                            .contentLength(resource.contentLength())
                            .body(new InputStreamResource(resource.getInputStream()));
                }
            }
        } catch (IOException e) {
            // Log error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // Return a default image or 404 if no picture or error
        // For now, just 404 if not found
        return ResponseEntity.notFound().build();
    }
}