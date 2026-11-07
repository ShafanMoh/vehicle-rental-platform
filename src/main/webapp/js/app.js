/* DriveEasy - small UI helpers */
(function () {
    "use strict";

    const money = (value) => "LKR " + Number(value).toLocaleString("en-LK", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    // Ask before running dangerous actions (delete, cancel ...)
    document.querySelectorAll("form[data-confirm]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            if (!window.confirm(form.dataset.confirm)) {
                event.preventDefault();
            }
        });
    });

    // Live price preview on the booking form
    const bookingForm = document.getElementById("bookingForm");
    if (bookingForm) {
        const start = bookingForm.querySelector("[name=startDate]");
        const end = bookingForm.querySelector("[name=endDate]");
        const box = document.getElementById("quoteBox");
        const quoteUrl = bookingForm.dataset.quoteUrl;

        const refreshQuote = async () => {
            if (!start.value || !end.value) {
                box.classList.add("d-none");
                return;
            }
            if (end.min !== start.value) {
                end.min = start.value;
            }
            try {
                const response = await fetch(quoteUrl + "?start=" + start.value + "&end=" + end.value);
                const quote = await response.json();
                box.classList.remove("d-none");
                if (quote.error) {
                    box.innerHTML = '<span class="text-danger"><i class="bi bi-exclamation-circle"></i> ' + quote.error + "</span>";
                    return;
                }
                let html = '<div class="d-flex justify-content-between"><span>' + quote.days + " day(s) incl. insurance</span><span>"
                    + money(quote.baseCost) + "</span></div>";
                if (quote.discount > 0) {
                    html += '<div class="d-flex justify-content-between text-success"><span>Premium discount</span><span>- '
                        + money(quote.discount) + "</span></div>";
                }
                html += '<hr class="my-2"><div class="d-flex justify-content-between total"><span>Total</span><span>'
                    + money(quote.total) + "</span></div>";
                box.innerHTML = html;
            } catch (e) {
                box.classList.add("d-none");
            }
        };
        start.addEventListener("change", refreshQuote);
        end.addEventListener("change", refreshQuote);
        refreshQuote();
    }

    // Show only the fields for the chosen payment method
    const methodInputs = document.querySelectorAll("input[name=method]");
    if (methodInputs.length) {
        const showMethod = () => {
            const chosen = document.querySelector("input[name=method]:checked");
            document.querySelectorAll("[data-method]").forEach((section) => {
                const active = chosen && section.dataset.method === chosen.value;
                section.classList.toggle("d-none", !active);
                section.querySelectorAll("input").forEach((input) => {
                    input.disabled = !active;
                });
            });
        };
        methodInputs.forEach((input) => input.addEventListener("change", showMethod));
        showMethod();
    }

    // Card number: add a space every 4 digits; expiry: add the slash
    const cardNumber = document.querySelector("[name=cardNumber]");
    if (cardNumber) {
        cardNumber.addEventListener("input", () => {
            const digits = cardNumber.value.replace(/\D/g, "").slice(0, 19);
            cardNumber.value = digits.replace(/(.{4})/g, "$1 ").trim();
        });
    }
    const expiry = document.querySelector("[name=expiry]");
    if (expiry) {
        expiry.addEventListener("input", () => {
            const digits = expiry.value.replace(/\D/g, "").slice(0, 4);
            expiry.value = digits.length > 2 ? digits.slice(0, 2) + "/" + digits.slice(2) : digits;
        });
    }

    // Vehicle form: show only the fields for the chosen vehicle type
    const typeSelect = document.getElementById("vehicleType");
    if (typeSelect) {
        const showTypeFields = () => {
            document.querySelectorAll("[data-types]").forEach((field) => {
                const visible = field.dataset.types.split(",").includes(typeSelect.value);
                field.classList.toggle("d-none", !visible);
            });
        };
        typeSelect.addEventListener("change", showTypeFields);
        showTypeFields();
    }

    // Live image preview on the vehicle form
    const imageInput = document.querySelector("[name=imageUrl]");
    const imagePreview = document.getElementById("imagePreview");
    if (imageInput && imagePreview) {
        const fallback = imagePreview.dataset.fallback;
        const updatePreview = () => {
            const chosenType = typeSelect ? typeSelect.value.toLowerCase() : "car";
            imagePreview.src = imageInput.value.trim() || fallback.replace("TYPE", chosenType);
        };
        imagePreview.addEventListener("error", () => {
            imagePreview.src = fallback.replace("TYPE", typeSelect ? typeSelect.value.toLowerCase() : "car");
        });
        imageInput.addEventListener("input", updatePreview);
        if (typeSelect) {
            typeSelect.addEventListener("change", updatePreview);
        }
        updatePreview();
    }

    // Role field on the admin user form
    const roleSelect = document.getElementById("roleSelect");
    if (roleSelect) {
        const showRoleFields = () => {
            document.querySelectorAll("[data-roles]").forEach((field) => {
                const visible = field.dataset.roles.split(",").includes(roleSelect.value);
                field.classList.toggle("d-none", !visible);
            });
        };
        roleSelect.addEventListener("change", showRoleFields);
        showRoleFields();
    }

    // Fade out success messages after a few seconds
    document.querySelectorAll(".alert-auto").forEach((alert) => {
        setTimeout(() => {
            const instance = window.bootstrap && bootstrap.Alert.getOrCreateInstance(alert);
            if (instance) {
                instance.close();
            }
        }, 6000);
    });
})();
