// admin-orders.js

$(document).ready(function() {
    // Initialize modals
    const updateStatusModal = new bootstrap.Modal('#updateStatusModal');
    const cancelOrderModal = new bootstrap.Modal('#cancelOrderModal');

    // Set up status modal
    $('#updateStatusModal').on('show.bs.modal', function(event) {
        const button = $(event.relatedTarget);
        const orderId = button.data('order-id');
        $('#statusOrderId').val(orderId);
    });

    // Set up cancel modal
    $('#cancelOrderModal').on('show.bs.modal', function(event) {
        const button = $(event.relatedTarget);
        const orderId = button.data('order-id');
        $('#cancelOrderId').val(orderId);
    });

    // Order search functionality
    $('#orderSearch').on('keyup', function() {
        const value = $(this).val().toLowerCase();
        $('.order-row').filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
        });
    });

    // Order filtering
    $('.filter-option').on('click', function() {
        const status = $(this).data('status');

        // Update active filter
        $('.filter-option').removeClass('active');
        $(this).addClass('active');

        // Update button text
        $('.btn-filter').html(`
            <i class="fas fa-filter me-2"></i>
            ${status === 'all' ? 'Filter' : $(this).text()}
        `);

        // Filter rows
        if (status === 'all') {
            $('.order-row').show();
        } else {
            $('.order-row').hide();
            $(`.order-row[data-status="${status}"]`).show();
        }
    });

    // Highlight table row on hover
    $('.order-row').on('mouseenter', function() {
        $(this).addClass('table-active');
    }).on('mouseleave', function() {
        $(this).removeClass('table-active');
    });

    // Initialize tooltips
    $('[data-bs-toggle="tooltip"]').tooltip();
});