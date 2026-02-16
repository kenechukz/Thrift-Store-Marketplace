document.addEventListener("DOMContentLoaded", () => {
    const cards = document.querySelectorAll(".catalogue-item-card[data-id]");

    cards.forEach(card => {
        const productId = card.getAttribute("data-id");

        card.addEventListener("click", () => {
            window.location.href = `/products/${productId}`;
        });

        const button = card.querySelector(".add-to-basket");
        if (button) {
            button.addEventListener("click", (e) => {
                e.stopPropagation();
                console.log("Added item to cart!");
            });
        }
    });
});