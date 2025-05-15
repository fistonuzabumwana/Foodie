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
                $feedback.html('<i class="fas fa-exclamation-circle text-danger"></i> File is too large (max 5MB)');
                $(this).val('');
                return;
            }

            // Check file type
            if (!file.type.match('image.*')) {
                $feedback.html('<i class="fas fa-exclamation-circle text-danger"></i> Only images are allowed');
                $(this).val('');
                return;
            }

            // Preview image
            const reader = new FileReader();
            reader.onload = function(e) {
                $feedback.html(`
                    <div class="d-flex align-items-center text-success">
                        <i class="fas fa-check-circle me-2"></i>
                        ${fileName} (${(file.size / 1024).toFixed(1)}KB)
                        <img src="${e.target.result}" class="img-thumbnail ms-3" style="max-height: 40px;">
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
            $('.form-error').first().parent().get(0).scrollIntoView({
                behavior: 'smooth',
                block: 'center'
            });
        }
    });

    // Animate form errors
    $('.form-error').each(function() {
        $(this).parent().addClass('animate__animated animate__headShake');
        setTimeout(() => {
            $(this).parent().removeClass('animate__animated animate__headShake');
        }, 1000);
    });
});