document.addEventListener("DOMContentLoaded", () => {
    const actionForms = document.querySelectorAll("form[data-basket-action-form]");
    const checkoutBtn = document.getElementById("basket-checkout-btn");

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

    if (!checkoutBtn) {
        return;
    }

    checkoutBtn.addEventListener("click", async () => {
        checkoutBtn.disabled = true;

        try {
            const response = await fetch("/checkout", {
                method: "POST",
                credentials: "same-origin"
            });
            const payload = await response.json().catch(() => ({}));

            if (!response.ok) {
                throw new Error(payload.error || `Checkout failed (${response.status})`);
            }

            showCheckoutMessage(`Order placed: ${payload.orderNumber || "success"}`);
            setTimeout(() => {
                window.location.href = "/basket";
            }, 1000);
        } catch (error) {
            console.error(error);
            showCheckoutMessage(error.message || "Checkout failed. Please try again.", true);
            checkoutBtn.disabled = false;
        }
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

function showCheckoutMessage(message, isError) {
    let statusEl = document.getElementById("checkout-status-message");
    if (!statusEl) {
        statusEl = document.createElement("p");
        statusEl.id = "checkout-status-message";
        const basketBody = document.querySelector(".basket-body");
        if (basketBody) {
            basketBody.appendChild(statusEl);
        }
    }

    statusEl.textContent = message;
    statusEl.style.color = isError ? "#b42318" : "#027a48";
    statusEl.style.fontWeight = "600";
}
