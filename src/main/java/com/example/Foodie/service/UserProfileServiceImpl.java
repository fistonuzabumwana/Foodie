package com.example.Foodie.service;

import com.example.Foodie.dto.AddressDto;
import com.example.Foodie.dto.UserProfileDataDto;
import com.example.Foodie.model.User;
import com.example.Foodie.model.UserProfile;
import com.example.Foodie.model.Address;
import com.example.Foodie.repository.UserProfileRepository;
import com.example.Foodie.repository.UserRepository;
import com.example.Foodie.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  UserRepository userRepository,
                                  FileStorageService fileStorageService) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getUserProfile(User user) throws ProfileNotFoundException {
        return userProfileRepository.findById(user.getId())
                .orElseThrow(() -> new ProfileNotFoundException(user.getId()));
    }

    @Override
    @Transactional
    public UserProfile getOrCreateUserProfile(User user) {
        try {
            return getUserProfile(user);
        } catch (ProfileNotFoundException e) {
            return createDefaultProfile(user);
        }
    }

    @Override
    @Transactional
    public UserProfile createDefaultProfile(User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile newProfile = new UserProfile(managedUser);
        managedUser.setUserProfile(newProfile);
        return userProfileRepository.save(newProfile);
    }

    @Override
    @Transactional
    public UserProfile updateUserProfile(User user, UserProfileDataDto profileDataDto,
                                         MultipartFile profilePictureFile) throws IOException {
        UserProfile userProfile = getOrCreateUserProfile(user);
        updateProfileFields(userProfile, profileDataDto);
        updateProfilePictureIfProvided(userProfile, profilePictureFile);
        return userProfileRepository.save(userProfile);
    }

    private void updateProfileFields(UserProfile profile, UserProfileDataDto dto) {
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setPhone(dto.getPhone());

        AddressDto addressDto = dto.getAddress();
        if (addressDto != null) {
            Address address = profile.getAddress() != null ?
                    profile.getAddress() : new Address();
            address.setStreetAddress(addressDto.getStreetAddress());
            address.setSector(addressDto.getSector());
            address.setDistrict(addressDto.getDistrict());
            address.setCountry(addressDto.getCountry());
            profile.setAddress(address);
        }
    }

    private void updateProfilePictureIfProvided(UserProfile profile, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String oldFileId = profile.getProfilePictureFileId();
            String newFileId = fileStorageService.store(file);
            profile.setProfilePictureFileId(newFileId);

            if (oldFileId != null && !oldFileId.isEmpty() && !oldFileId.equals(newFileId)) {
                try {
                    fileStorageService.delete(oldFileId);
                } catch (Exception e) {
                    System.err.println("Failed to delete old profile picture: " + oldFileId);
                    e.printStackTrace();
                }
            }
        }
    }
}