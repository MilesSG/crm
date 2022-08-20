layui.use(['form', 'jquery', 'jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    // layui 用户登录 表单提交
    form.on('submit(login)', function (data) {
        // 获取表单元素 用户名+密码
        data = data.field;
        console.log(data);

        /**
         *  用户名 密码 非空校验
         */
        if (data.username == "undefined" || data.username == "" || data.username.trim() == "") {
            layer.msg("用户名不能为空!");
            return false;
        }

        if (data.password == "undefined" || data.password == "" || data.password.trim() == "") {
            layer.msg("用户密码不能为空!");
            return false;
        }

        $.ajax({
            type: "post",
            url: ctx + "/user/login",
            data: {
                userName: data.username,
                userPwd: data.password
            },
            dataType: "json",
            success: function (result) {
                if (data.code == 200) {
                    layer.msg("用户登录成功", function () {
                        // 判断用户是否选择记住密码（判断复选框是否被选中，如果选中，则设置cookie对象7天生效）
                        if ($("#rememberMe").prop("checked")) {
                            // 选中，则设置cookie对象7天生效
                            // 将用户信息设置到cookie中
                            $.cookie("userIdStr", result.result.userIdStr, {expires: 7});
                            $.cookie("userName", result.result.userName, {expires: 7});
                            $.cookie("trueName", result.result.trueName, {expires: 7});
                        } else {
                            // 将用户信息设置到cookie中
                            $.cookie("userIdStr", result.result.userIdStr);
                            $.cookie("userName", result.result.userName);
                            $.cookie("trueName", result.result.trueName);
                        }
                        window.location.href = ctx + "/main";
                    })
                } else {
                    layer.msg(result.msg, {icon: 5});
                }
            }
        });
        return false;
    })


});