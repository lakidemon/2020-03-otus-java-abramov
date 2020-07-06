<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>Авторизация</title>

    <!-- Bootstrap core CSS -->
    <link href="/static/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="/static/css/signin.css" rel="stylesheet">

    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/bootstrap.js"></script>
</head>

<body class="text-center">
<form class="form-signin" method="post" action="/login">
    <h1 class="h3 mb-3 font-weight-normal">Авторизация</h1>
    <label for="username" class="sr-only">Логин</label>
    <input name="username" type="text" id="username" class="form-control" placeholder="Имя пользователя" required autofocus>
    <label for="password" class="sr-only">Пароль</label>
    <input name="password" type="password" id="password" class="form-control" placeholder="Пароль" required>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Войти</button>
</form>
<!-- Modal -->
<div class="modal fade" id="errorMessage" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLongTitle">Ошибка</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Логин или пароль неверны
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-danger" data-dismiss="modal">Ладно</button>
            </div>
        </div>
    </div>
</div>
<script>
    <#if failed??>
    $('#errorMessage').modal('show');
    </#if>
</script>
</body>
</html>
