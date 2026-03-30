function handleResult(resultData) {
    let itemsList = $("#items-table-body");

    resultData["items"].forEach(item => {
        let row = `
            <tr>
                <td>
                    <a href="single-movie.html?id=${item['id']}">${item['title']}</a>
                </td>
                <td>
                   <button>-</button>
                   ${item['quantity']}
                   <button>+</button>
                </td>
                <td>
                    <button>Remove</button>
                </td>
                <td>${item['price']}</td>
                <td>${item['subtotal']}</td>
            </tr>
        `;

        itemsList.append(row);
    });

    let itemsTable = $("#items-table");

    let footer = `
        <tfoot>
            <tr>
                <td colspan="3"></td>
                <td class="text-end text-dark fw-bold">Total:</td>
                <td>${resultData["total"]}</td>
            </tr>
        </tfoot>
    `;

    itemsTable.append(footer);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cart",
    success: (resultData) => handleResult(resultData)
});