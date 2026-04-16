const starsForm = $("#stars-form");
const alertSuccess  = $("#alert-success");
const alertFailure = $("alert-failure");

const baseURL = window.location.origin + '/' + window.location.pathname.split('/')[1];

function handleSuccess() {
    alertFailure.addClass("d-none");

    alertSuccess.text("Successfully added star!");
    alertSuccess.removeClass("d-none");
}

function handleFailure(jqXHR) {
    alertSuccess.addClass("d-none");

    alertFailure.text(jqXHR.responseJSON.message ?? "Something went wrong. Please try again.");
    alertFailure.removeClass("d-none");
}

function handleFormSubmit(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax({
        url: baseURL + "/api/employees/star",
        method: "POST",
        data: starsForm.serialize(),
        success: handleSuccess,
        error: (jqXHR) => handleFailure(jqXHR)
    });
}

starsForm.submit(handleFormSubmit);