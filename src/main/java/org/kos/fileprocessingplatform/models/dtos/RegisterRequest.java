package org.kos.fileprocessingplatform.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kos.fileprocessingplatform.validation.PasswordMatches;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class RegisterRequest {
    @NotBlank
    @Size(max = 50)
    String username;
    @NotBlank
    @Email
    @Size(max = 255)
    String email;
    @NotBlank
    @Size(min = 8, max = 72)
    String password;
    @NotBlank
    @Size(min = 8, max = 72)
    String confirmPassword;
}
