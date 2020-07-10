<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    <p>Perform Login to Microweb Sample:</p>
    <form method="post">
        <#if error >
            <p><strong>${errorMessage}</strong></p>
        </#if>
        <p>Name: <input type="text" name="name"/></p>
        <p>Password: <input type="password" name="password"/></p>
        <p><input type="submit" name="log in"/></p>
    </form>
</body>
</html>