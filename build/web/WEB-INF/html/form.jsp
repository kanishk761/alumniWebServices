<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Ajax</title>
    </head>
    <body>
        <h1>alumni</h1>
        <input type="hidden" id="token" value="ew0KIOKAnGFsZ+KAnSA6IOKAnEhTMjU24oCdLA0KIOKAnHR5cOKAnSA6IOKAnEpXVOKAnQ0KfQ0K.ew0KIOKAnHVpZOKAnSA6IOKAnGFtaXTigJ0sDQog4oCcSUROdW1iZXLigJ0gOiAxNDIyNzc5NjM4LA0KIOKAnGVtcGxveWVlVHlwZeKAnSA6IOKAnEZhY3VsdHnigJ0sDQog4oCcb3XigJ06IOKAnEVFQ1PigJ0sDQog4oCcZGlzcGxheU5hbWXigJ06IOKAnEFtaXQgSyBEaGFy4oCdLA0KIOKAnHJhbmRvbU51bWJlcuKAnSA6IOKAnFhYWFhYWFhY4oCdDQp9DQo=.NWUyM2NlMGRkZjY5NzA2MWZiMDRiODAzODlkZDBhMzE4NGZhM2I0NjdhYWFkYjMyZGZhYTkwZmY1NzUxY2M5Yw">
        Name:
        <input type="text" name="name" id= "name" ><br><br>
        DOB:
        <input type="date" name="dob" id= "dob" ><br><br>
        ID number:
        <input type="text" name="idNo" id= "idNo" ><br><br>
        <input type="submit" name="sub" id="sub"><br><br>
       
        <div id="msg"></div>
    </body>
    
    <script src="jQuery.js"></script>
    <script>
        $(document).ready(function() 
            {
                $("#sub").click(function() 
                {
                    var name = $("#name").val();
                    var idNo = $("#idNo").val();
                    var dob = $("#dob").val();
                    //var d = {ID: idNo, name: name, DOB: dob};
                    console.log("hello");
                    var params = generateParameter(name,idNo,dob);
                    var serviceRequest = generateServiceRequest($("#token").val(), "secret123", params, "student", "UserService", "addUser");
                    $.ajax({
                        type : "POST",
                        url : "/alumni/ops/saveData",
                        data : serviceRequest,
                        contentType : "application/xml",
                        dataType : "html",
                        error : function(){
                            $("#msg").html("some problem"); 
                        },
                        success : function(res){
                            console.log(res);
                            $("#msg").html(res);
                        }
                    });
                });
            }); 
    function generateParameter(name, id, DOB){
		var param = "<parameters>";
		param = param.concat('<parameter name="name" value="',name,'" />');
		param = param.concat('<parameter name="ID" value="',id,'" />');
		param = param.concat('<parameter name="DOB" value="',DOB,'" />');
		param = param.concat('</parameters>');

		return param;
	}

	function generateServiceRequest(token, secret, param, role, serviceName, operationName) {
	    var serviceReq = "";
	    serviceReq = serviceReq.concat('<role>', role, '</role>', '<token>', token, '</token>', '<serviceName>', serviceName, '</serviceName>', '<operationName>', operationName, '</operationName>');
	    if (param == "") {
	        serviceReq = serviceReq.concat('<parameters></parameters>');
	    } else {
	        serviceReq = serviceReq.concat(param);
	    }
	    var finalServiceReq = '';
	    var hash = hashServiceReq(secret, serviceReq);
	    finalServiceReq = finalServiceReq.concat('<?xml version=\"1.0\" encoding=\"UTF-8\"?>', '<serviceRequest>', serviceReq, '<hashCode>', hash, '</hashCode>', '</serviceRequest>');
	    return finalServiceReq;
	}

	function hashServiceReq(secret, serviceReq) {
	    var hash = CryptoJS.HmacSHA256(serviceReq, secret);
	    var hashInBase64 = CryptoJS.enc.Base64.stringify(hash);
	    return hashInBase64;
	}
</script>
<script src="cryptoJs.js"></script>
<script src="cryptoHmacSha256.js"></script>
<script src="cryptoJsBase64Encode.js"></script>
</html>
