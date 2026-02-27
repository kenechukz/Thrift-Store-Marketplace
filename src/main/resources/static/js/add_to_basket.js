document.addEventListener("DOMContentLoaded", () => {
    const cards = document.querySelectorAll(".catalogue-item-card[data-id], .item-card");

    cards.forEach(card => {
        const productId = card.getAttribute("data-id");

        card.addEventListener("click", () => {
            window.location.href = `/products/${productId}`;
        });

        const button = card.querySelector(".add-to-basket");
        if (button) {
            button.addEventListener("click",  async (e) => {
                e.stopPropagation();


                const response = await fetch(`/basket/${productId}`, {
                    method: "POST",
                    credentials: "same-origin"
                });

                if(!response.ok){
                    console.error("Failed to add item to cart", response.status);
                    return;
                }

                const payload = await response.json().catch(() => ({}));
                if (typeof payload.basketCount === "number") {
                    updateNavBasketBadge(payload.basketCount);
                }
            });
        }
    });

    const productPageButton = document.querySelector(".product-add-to-basket[data-id]");
    if (productPageButton) {
        productPageButton.addEventListener("click", async () => {
            const productId = productPageButton.getAttribute("data-id");
            if (!productId) {
                return;
            }

            const response = await fetch(`/basket/${productId}`, {
                method: "POST",
                credentials: "same-origin"
            });

            if (!response.ok) {
                console.error("Failed to add item to cart", response.status);
                return;
            }

            const payload = await response.json().catch(() => ({}));
            if (typeof payload.basketCount === "number") {
                updateNavBasketBadge(payload.basketCount);
            }
        });
    }
});

function updateNavBasketBadge(count) {
    const badge = document.getElementById("basket-nav-count");
    if (!badge) {
        return;
    }

    badge.textContent = String(count);
    badge.classList.toggle("hidden", count <= 0);
}
