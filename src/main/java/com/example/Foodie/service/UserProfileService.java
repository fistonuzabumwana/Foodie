package com.example.Foodie.service;

import com.example.Foodie.dto.UserProfileDataDto;
import com.example.Foodie.model.User;
import com.example.Foodie.model.UserProfile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface UserProfileService {
    UserProfile getUserProfile(User user) throws ProfileNotFoundException;
    UserProfile getOrCreateUserProfile(User user);
    UserProfile updateUserProfile(User user, UserProfileDataDto profileDataDto,
                                  MultipartFile profilePictureFile) throws IOException;
    UserProfile createDefaultProfile(User user);

    class ProfileNotFoundException extends RuntimeException {
        public ProfileNotFoundException(Long userId) {
            super("Profile not found for user: " + userId);
        }
    }
}