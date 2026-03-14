function scrollCarousel(trackId, direction) {
    const track = document.getElementById(trackId + '-track');
    track.scrollBy({ left: direction * 500, behavior: 'smooth' });
}

async function selectFlight(event, form) {
    event.preventDefault();

    // remove any other selected cards from carousel
    const track = form.closest('.carousel-track');
        track.querySelectorAll('.flight-card').forEach(card => {
        card.classList.remove('flight-card--selected');
    });

    // mark card as selected
    form.closest('.flight-card').classList.add('flight-card--selected');

    const data = new FormData(form);
    await fetch('/flights/select', { method: 'POST', body: data });
}