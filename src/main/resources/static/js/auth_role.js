document.addEventListener("DOMContentLoaded", () => {
    const forms = document.querySelectorAll("form.auth-panel");

    forms.forEach((form) => {
        const target = form.querySelector("input[data-role-target]");
        const choices = form.querySelectorAll("input[data-role-choice]");

        if (!target || choices.length === 0) {
            return;
        }

        const setTargetFromChecked = () => {
            const checked = form.querySelector("input[data-role-choice]:checked");
            target.value = checked ? checked.value : "USER";
        };

        choices.forEach((choice) => {
            choice.addEventListener("change", setTargetFromChecked);
        });

        form.addEventListener("submit", setTargetFromChecked);
        setTargetFromChecked();
    });
});
