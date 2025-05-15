$(document).ready(function() {
    // Category filter
    $('.category-btn').click(function() {
        $('.category-btn').removeClass('active');
        $(this).addClass('active');

        const category = $(this).data('category');
        if (category === 'all') {
            $('.product-card').show();
        } else {
            $('.product-card').hide();
            $(`.product-card[data-category="${category}"]`).show();
        }
    });

    // Favorite button toggle
    $('.favorite-btn').click(function() {
        $(this).toggleClass('active');
        $(this).find('i').toggleClass('far fas');

        if ($(this).hasClass('active')) {
            // Optional: Send AJAX request to save favorite
            console.log('Added to favorites');
        } else {
            console.log('Removed from favorites');
        }
    });

    // Search functionality
    $('.search-input').on('input', function() {
        const searchTerm = $(this).val().toLowerCase();

        $('.product-card').each(function() {
            const productName = $(this).find('.product-title').text().toLowerCase();
            const productDesc = $(this).find('.product-desc').text().toLowerCase();

            if (productName.includes(searchTerm) || productDesc.includes(searchTerm)) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });

    // Sort functionality
    $('.form-select').change(function() {
        const sortBy = $(this).val();
        const $container = $('.product-grid');
        const $items = $('.product-card');

        $items.sort(function(a, b) {
            if (sortBy === 'Price: Low to High') {
                const priceA = parseFloat($(a).find('.product-price').text().replace('$', ''));
                const priceB = parseFloat($(b).find('.product-price').text().replace('$', ''));
                return priceA - priceB;
            } else if (sortBy === 'Price: High to Low') {
                const priceA = parseFloat($(a).find('.product-price').text().replace('$', ''));
                const priceB = parseFloat($(b).find('.product-price').text().replace('$', ''));
                return priceB - priceA;
            } else if (sortBy === 'Alphabetical') {
                const textA = $(a).find('.product-title').text();
                const textB = $(b).find('.product-title').text();
                return textA.localeCompare(textB);
            }
            // Default: Popular (original order)
            return 0;
        });

        $container.append($items);
    });

    // Add to cart animation
    $('.btn-add-to-cart').click(function(e) {
        e.preventDefault();
        const $button = $(this);

        // Animation
        $button.html('<i class="fas fa-check"></i> Added');
        $button.css('background', '#28a745');

        // Submit form after animation
        setTimeout(() => {
            $button.closest('form').submit();
        }, 500);
    });
});