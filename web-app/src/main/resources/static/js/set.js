document.addEventListener('DOMContentLoaded', function () {
    const terms = document.querySelectorAll('[data-term]');
    terms.forEach(term => {
        term.innerHTML = term.innerHTML.replace(/\n/g, "<br>");
    });
});