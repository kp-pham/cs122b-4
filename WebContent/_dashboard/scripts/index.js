const baseURL = window.location.origin + '/' + window.location.pathname.split('/')[1];

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: baseURL + "/api/employees/schema",
    success: (resultData) => handleResult(resultData)
});