const baseURL = window.location.origin + '/' + window.location.pathname.split('/')[1];

function handleResult(resultData) {
    const schemas = $("#schemas");

    resultData.forEach(table => {
        let tableHTML = `
            <h3>${table["name"]}</h3>
            <table id="${table["name"]}-table" class="table table-striped">
                <thead>
                <tr>
                    <th>Attribute</th>
                    <th>Type</th>
                </tr>
                </thead>
                <tbody id="${table["name"]}-table-body">
                    ${table["columns"].map(column => {
                        return `
                            <tr>
                                <td>${column["name"]}</td>
                                <td>${column["type"]}</td> 
                            </tr>      
                        `;
                    }).join("")}
                </tbody>
            </table>
        `;

        schemas.append(tableHTML);
    });
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: baseURL + "/api/employees/schema",
    success: (resultData) => handleResult(resultData)
});