const paymentForm = $("#payment-form");

function handleResult(resultData) {
    $("#total").text(`Order Total: $${resultData["total"].toFixed(2)}`);
}

function submitPaymentForm(formSubmitEvent) {
    jQuery.ajax({
        dataType: "json",
        method: "POST",
        data: paymentForm.serialize(),
        url: "api/transactions",
        success: (resultData) => handlePaymentResult(resultData),
        failure: (resultData) => handlePaymentFailed(resultData)
    });
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/transactions",
    success: (resultData) => handleResult(resultData)
});

paymentForm.submit(submitPaymentForm);