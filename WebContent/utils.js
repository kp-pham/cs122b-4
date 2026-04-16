const navPlaceholder = $("#nav-placeholder");
navPlaceholder.load("nav.html");

const logoutForm = $("#logout-form");

const baseURL = window.location.origin + '/' + window.location.pathname.split('/')[1];

function handleLogoutFormSubmit(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax({
        url: baseURL + "/api/logout",
        method: "POST"
    });
}

logoutForm.submit(handleLogoutFormSubmit);
