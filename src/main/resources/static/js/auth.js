document.addEventListener('DOMContentLoaded', function() {
    // Password toggle functionality
    const passwordToggle = document.querySelector('.password-toggle');
    if (passwordToggle) {
        passwordToggle.addEventListener('click', function() {
            const passwordInput = document.getElementById('password');
            const icon = this.querySelector('i');

            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                passwordInput.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        });
    }

    // Form input animations
    const inputs = document.querySelectorAll('.floating-input input');
    inputs.forEach(input => {
        // Add focus/blur events
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
        });

        input.addEventListener('blur', function() {
            if (!this.value) {
                this.parentElement.classList.remove('focused');
            }
        });

        // Check if input has value on page load
        if (input.value) {
            input.parentElement.classList.add('focused');
        }
    });

    // Social login buttons
    const socialButtons = document.querySelectorAll('.btn-social');
    socialButtons.forEach(button => {
        button.addEventListener('mouseenter', function() {
            const icon = this.querySelector('i');
            icon.style.transform = 'scale(1.2)';
        });

        button.addEventListener('mouseleave', function() {
            const icon = this.querySelector('i');
            icon.style.transform = 'scale(1)';
        });
    });
});


// Password strength indicator
function checkPasswordStrength(password) {
    let strength = 0;

    // Length check
    if (password.length >= 8) strength++;
    // Contains numbers
    if (password.match(/\d/)) strength++;
    // Contains special chars
    if (password.match(/[^a-zA-Z0-9]/)) strength++;

    return strength;
}

// Password strength visualization
function updatePasswordStrength() {
    const passwordInput = document.getElementById('password');
    const strengthMeter = document.querySelector('.strength-meter');
    const strengthText = document.querySelector('.strength-text span');
    const strengthSections = document.querySelectorAll('.strength-section');
    const passwordStrength = document.querySelector('.password-strength');

    if (!passwordInput || !strengthMeter) return;

    passwordInput.addEventListener('input', function() {
        const password = this.value;
        const strength = checkPasswordStrength(password);

        if (password.length > 0) {
            passwordStrength.style.display = 'block';
        } else {
            passwordStrength.style.display = 'none';
            return;
        }

        // Reset all sections
        strengthSections.forEach(section => {
            section.style.backgroundColor = '#e0e0e0';
        });

        // Update strength meter and text
        if (strength > 0) {
            for (let i = 0; i < strength; i++) {
                let color;
                if (strength === 1) {
                    color = '#dc3545'; // Red for weak
                    strengthText.textContent = 'Weak';
                } else if (strength === 2) {
                    color = '#fd7e14'; // Orange for medium
                    strengthText.textContent = 'Medium';
                } else {
                    color = '#28a745'; // Green for strong
                    strengthText.textContent = 'Strong';
                }
                strengthSections[i].style.backgroundColor = color;
            }
        } else {
            strengthText.textContent = 'Very Weak';
        }
    });
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Previous login page JS...

    // Registration page specific JS
    updatePasswordStrength();

    // Terms checkbox validation
    const registerForm = document.querySelector('.register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            const termsCheckbox = document.getElementById('terms');
            if (!termsCheckbox.checked) {
                e.preventDefault();
                termsCheckbox.focus();
                // Add visual feedback
                termsCheckbox.parentElement.classList.add('shake');
                setTimeout(() => {
                    termsCheckbox.parentElement.classList.remove('shake');
                }, 500);
            }
        });
    }
});

