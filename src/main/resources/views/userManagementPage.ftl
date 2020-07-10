<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/index.css">
    <title>Microweb Sample</title>
</head>
<body>
    <p>User Management</p>
    <p>Welcome <strong>${user.name}</strong>!</p>
    <p>Return to <a href="/v0">home</a></p>
    <hr/>
    <form method="post" action="/v0/gui-user">
        <table>
            <tr>
                <td>Name:</td>
                <td style="padding-left: 10px"><input type="text" name="name"/></td>
            </tr>
            <tr>
                <td>Password:</td>
                <td style="padding-left: 10px"><input type="password" name="password"/></td></tr>
            <tr>
                <td>Password confirmation:</td>
                <td style="padding-left: 10px"><input type="password" name="passConfirmation"/></td>
            </tr>
            <tr>
                <td>Given name:</td>
                <td style="padding-left: 10px"><input type="text" name="givenName"/></td>
            </tr>
            <tr>
                <td>Family name:</td>
                <td style="padding-left: 10px"><input type="text" name="familyName"/></td>
            </tr>
            <tr>
                <td>Role:</td>
                <td style="padding-left: 10px">
                    <select name="role">
                        <#list roles as r>
                        <option value="${r.name}">${r.name}</option>
                        </#list>
                    </select>
                </td>
            </tr>
        </table>
        <input type="submit" value="Create"/>
    </form>
    <hr/>
    <table style="width: 100%">
        <tr>
            <td>Name:</td>
            <td>Roles:</td>
            <td>Add Role:</td>
        </tr>
        <#list users as u>
            <tr>
                <td>${u.name}</td>
                <td>
                    <#list u.roles as r>
                        <strong style="color: gray">[</strong>${r.name}<strong style="color: gray">]</strong>&nbsp;
                    </#list>
                </td>
                <td>
                    <form action="/v0/gui-user/${u.id}/role" method="post">
                        <input type="hidden" name="userId" value="${u.id}"/>
                        <select name="role">
                            <#list roles as r>
                                <option value="${r.name}">${r.name}</option>
                            </#list>
                        </select>
                        <input type="submit" value="&#8594;"/>
                    </form>
                </td>
            </tr>
        </#list>
    </table>
</body>
</html>