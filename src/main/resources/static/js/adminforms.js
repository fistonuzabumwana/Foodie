// admin-forms.js

$(document).ready(function() {
    // File upload feedback
    $('.file-upload-input').on('change', function() {
        const fileName = $(this).val().split('\\').pop();
        const $feedback = $(this).siblings('.file-upload-feedback');
        const maxSize = $(this).data('max-size');
        const file = this.files[0];

        if (file) {
            // Check file size
            if (file.size > maxSize) {
                $feedback.html('<div class="text-danger"><i class="fas fa-exclamation-circle me-2"></i> File is too large (max 5MB)</div>');
                $(this).val('');
                return;
            }

            // Check file type
            if (!file.type.match('image.*')) {
                $feedback.html('<div class="text-danger"><i class="fas fa-exclamation-circle me-2"></i> Only images are allowed</div>');
                $(this).val('');
                return;
            }

            // Preview image
            const reader = new FileReader();
            reader.onload = function(e) {
                $feedback.html(`
                    <div class="d-flex align-items-center text-success">
                        <i class="fas fa-check-circle me-2"></i>
                        <div>
                            <div>${fileName}</div>
                            <small>${(file.size / 1024).toFixed(1)}KB</small>
                        </div>
                        <img src="${e.target.result}" class="img-thumbnail ms-3">
                    </div>
                `);
            }
            reader.readAsDataURL(file);
        } else {
            $feedback.html('');
        }
    });

    // Drag and drop for file upload
    const $fileUploadLabel = $('.file-upload-label');

    $fileUploadLabel.on('dragover', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).addClass('dragover');
    });

    $fileUploadLabel.on('dragleave', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('dragover');
    });

    $fileUploadLabel.on('drop', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('dragover');

        const files = e.originalEvent.dataTransfer.files;
        if (files.length) {
            $('.file-upload-input')[0].files = files;
            $('.file-upload-input').trigger('change');
        }
    });

    // Form validation
    $('form').on('submit', function(e) {
        let isValid = true;

        // Validate required fields
        $(this).find('[required]').each(function() {
            if (!$(this).val()) {
                $(this).addClass('is-invalid');
                isValid = false;
            } else {
                $(this).removeClass('is-invalid');
            }
        });

        // Validate price
        const price = parseFloat($('#price').val());
        if (isNaN(price) || price <= 0) {
            $('#price').addClass('is-invalid');
            isValid = false;
        }

        // Validate stock
        const stock = parseInt($('#stockQuantity').val());
        if (isNaN(stock) || stock < 0) {
            $('#stockQuantity').addClass('is-invalid');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
            $('.is-invalid').first().get(0).scrollIntoView({
                behavior: 'smooth',
                block: 'center'
            });

            // Add animation to errors
            $('.is-invalid').each(function() {
                $(this).addClass('animate__animated animate__headShake');
                setTimeout(() => {
                    $(this).removeClass('animate__animated animate__headShake');
                }, 1000);
            });
        }
    });

    // Initialize tooltips
    $('[data-bs-toggle="tooltip"]').tooltip();
});