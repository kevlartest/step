let slideIndex = 0;
let slides = document.getElementsByClassName("slides");
let indicators = document.getElementsByClassName("indicator");

function nextSlide() {
  displaySlide(++slideIndex);
}

function previousSlide() {
  displaySlide(--slideIndex);
}

function setSlide(n) {
  displaySlide(slideIndex = n);
}

function displaySlide(n) {
    // Loop around if we reach the last slide
    slideIndex = (n + slides.length) % slides.length;

    Array.from(slides).forEach(slide => slide.style.display = "none");
    Array.from(indicators).forEach(indicator => indicator.className = indicator.className.replace(" active", ""));

    slides[slideIndex].style.display = "block";
    indicators[slideIndex].className += " active";
}

// Allow interacting with arrow keys
document.addEventListener('keydown', function(event) {
    switch (event.key) {
        case "ArrowLeft":
        previousSlide();
        break;

        case "ArrowRight":
        nextSlide();
        break;

        case "ArrowUp":
        setSlide(0);
        break;

        case "ArrowDown":
        setSlide(slides.length - 1);
        break;
  }
});