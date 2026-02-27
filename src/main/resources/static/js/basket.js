document.addEventListener("DOMContentLoaded", () => {
    const actionForms = document.querySelectorAll("form[data-basket-action-form]");
    if (actionForms.length === 0) {
        return;
    }

    actionForms.forEach((form) => {
        form.addEventListener("submit", async (event) => {
            event.preventDefault();

            const action = form.getAttribute("action");
            if (!action) {
                return;
            }

            const apiAction = action.replace("/basket/", "/basket/api/");

            try {
                const response = await fetch(apiAction, {
                    method: "POST",
                    credentials: "same-origin"
                });

                if (!response.ok) {
                    throw new Error(`Basket update failed (${response.status})`);
                }

                const payload = await response.json();
                updateBasketView(payload);
            } catch (error) {
                console.error(error);
                // Fallback to standard form submission if JS update fails.
                form.submit();
            }
        });
    });
});

function updateBasketView(payload) {
    const productId = String(payload.productId);
    const row = document.querySelector(`.basket-item[data-product-id="${productId}"]`);

    if (payload.removed) {
        row?.remove();
    } else if (row) {
        const quantityEl = row.querySelector(".js-item-quantity");
        const itemSubtotalEl = row.querySelector(".js-item-subtotal");

        if (quantityEl) {
            quantityEl.textContent = String(payload.quantity);
        }
        if (itemSubtotalEl) {
            itemSubtotalEl.textContent = `\u20AC${payload.itemSubtotal}`;
        }
    }

    const basketSubtotalEl = document.getElementById("basket-subtotal");
    const basketShippingEl = document.getElementById("basket-shipping");
    const basketTotalEl = document.getElementById("basket-total");
    const emptyMsgEl = document.getElementById("basket-empty-message");
    const checkoutBtn = document.getElementById("basket-checkout-btn");

    if (basketSubtotalEl) {
        basketSubtotalEl.textContent = `\u20AC${payload.subTotal}`;
    }

    if (basketShippingEl) {
        basketShippingEl.textContent = payload.basketCount === 0 ? "" : `\u20AC${payload.shipping}`;
    }

    if (basketTotalEl) {
        basketTotalEl.textContent = `\u20AC${payload.total}`;
    }

    if (emptyMsgEl) {
        emptyMsgEl.style.display = payload.basketCount === 0 ? "" : "none";
    }

    if (checkoutBtn) {
        checkoutBtn.style.display = payload.basketCount === 0 ? "none" : "";
    }
}

const checkoutBtn = document.getElementById("basket-checkout-btn");
checkoutBtn.addEventListener("click", async () => {
    
    const request = await fetch("/checkout", {
        method: "POST",
        credentials: "same-origin",
    });

})
