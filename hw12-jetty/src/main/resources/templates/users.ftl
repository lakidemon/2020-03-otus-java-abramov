<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>Панель управления пользователями</title>

    <!-- Bootstrap core CSS -->
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="/static/css/users.css" rel="stylesheet">

    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/bootstrap.js"></script>
</head>

<body>
<div class="container">

    <h3>Добавить пользователя</h3>
    <div class="card" style="width: 18rem;">
        <form id="add-form" method="post" action="/">
            <input name="name" type="text" id="name" class="form-control" placeholder="Имя" required autofocus>
            <input name="age" type="number" id="age" class="form-control" placeholder="Возраст" required>
            <input name="address" type="text" id="address" class="form-control" placeholder="Адрес" required>
            <div class="input-group">
                <input name="phone" type="tel" id="phone" class="form-control" placeholder="Телефон" required>
                <div class="input-group-append">
                    <button class="btn btn-outline-secondary" type="button" onclick="addPhone()">+</button>
                </div>
            </div>
            <button id="submit-button" class="btn btn-lg btn-primary btn-block" type="submit">Добавить</button>
        </form>
    </div>
    <h3>Список пользователей</h3>
    <table class="table table-light">
        <thead class="table-dark">
        <tr>
            <th scope="col">ID</th>
            <th scope="col">Имя</th>
            <th scope="col">Возраст</th>
            <th scope="col">Адрес</th>
            <th scope="col">Телефон</th>
        </tr>
        </thead>
        <tbody>
        <#list users as user>
            <tr>
                <th scope="row">${user.id}</th>
                <td>${user.name}</td>
                <td>${user.age}</td>
                <td>${user.address.street}</td>
                <td>${user.phones?map(phone -> phone.number)?join(";")}</td>
            </tr>
        </#list>
        </tbody>
    </table>
    <hr>
    <P class="text-center"><a href="/logout">Выйти</a></P>
</div> <!-- /container -->
<script>
    function addPhone() {
        var phoneClone = $('#phone').clone();
        phoneClone.val('');
        $('#submit-button').before(phoneClone);
    }
</script>
</body>
</html>
