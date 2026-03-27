let login_form = $("#login_form");

fuction handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);


}

function submitLoginForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/login", {
            method: "POST",
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}

login_form.submit(submitLoginForm);