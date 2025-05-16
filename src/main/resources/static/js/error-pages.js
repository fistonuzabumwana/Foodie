// error-pages.js

$(document).ready(function() {
    // Add animation to error code
    const errorCode = $('.error-code');

    // Pulsing animation every 3 seconds
    setInterval(() => {
        errorCode.removeClass('animate__pulse');
        setTimeout(() => {
            errorCode.addClass('animate__pulse');
        }, 50);
    }, 3000);

    // Toggle technical details
    $('[data-bs-toggle="collapse"]').on('click', function() {
        $(this).find('i').toggleClass('fa-chevron-down fa-chevron-up');
    });

    // If there are error details, show the info button with a pulsing animation
    if ($('.error-details-card').length) {
        $('.error-tech-info .btn-link i').addClass('animate__animated animate__pulse animate__infinite');

        // Stop animation after first click
        $('[data-bs-toggle="collapse"]').on('click', function() {
            $(this).find('i').removeClass('animate__pulse animate__infinite');
        });
    }
});

// 404 Specific JavaScript
if ($('body').hasClass('error-404')) {
    // Add animation to search form when focused
    $('.search-form input').on('focus', function() {
        $(this).parent().addClass('animate__animated animate__pulse');
    }).on('blur', function() {
        $(this).parent().removeClass('animate__animated animate__pulse');
    });

    // Make food icons float more dramatically on hover
    $('.floating-food').hover(
        function() {
            $(this).css('animation', 'float 1.5s ease-in-out infinite');
        },
        function() {
            $(this).css('animation', '');
        }
    );
}