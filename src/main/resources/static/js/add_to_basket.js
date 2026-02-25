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
                }

                console.log("Added item to cart!");
            });
        }
    });
});
