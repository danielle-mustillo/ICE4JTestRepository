var http = require('http');
var fs = require('fs');

var poster1 = null;
var poster2 = null;

var server = http.createServer( function(req, res) {

    console.dir(req.param);

    if (req.method == 'POST') {
	console.log("POST");
	var buf = '';
	req.on('data', function (data) {
	    buf += data;
	    console.log("Partial body: " + buf);
	});
	req.on('end', function () {
	    console.log("Body: " + buf);

	    // Parse json body and add the body to the correct poster.
	    var parsedBuf = JSON.parse(buf);
	    
	    if(parsedBuf.poster === "1") {
		console.log("Recieved : " + parsedBuf.poster + ". Adding to poster1");
		poster1 = parsedBuf.body;
	    } else {
		console.log("Recieved : " + parsedBuf.poster + ". Adding to poster2");
		poster2 = parsedBuf.body;
	    }
	    
	    res.writeHead(200, {'Content-Type': 'text/html'});
	    res.end('post received');

	});

    }
    else
    {
	console.log("GET");

	var buf = '';
	req.on('data', function (data) {
	    buf += data;
	    console.log("Partial body: " + buf);
	});
	req.on('end', function () {
	    console.log("Body: " + buf);

	    // Parse json body and add the body to the correct poster.
	    var parsedBuf = JSON.parse(buf);

	    var sendBack = '';
	    if(parsedBuf.poster === "1") {
		console.log("Recieved : " + parsedBuf.poster + ". Giving back poster2");
		sendBack = poster1;
	    } else {
		console.log("Recieved : " + parsedBuf.poster + ". Giving back poster1");
		sendBack = poster2;
	    }

	    res.writeHead(200, {'Content-Type': 'text/html'});
	    res.end(sendBack);
	});
	
    }

});

var port = 3000;
var host = '192.168.0.104';
server.listen(port, host);
console.log('Listening at http://' + host + ':' + port);
