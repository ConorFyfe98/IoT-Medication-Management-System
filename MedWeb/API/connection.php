	<?php
	try{
		$host = '';
		$dbname = '';
		$username = '';
		$password = '';
		
		$conn = new PDO('mysql:host='.$host.';dbname='.$dbname.';charset=utf8',$username,$password);
		echo "";
	} catch(PDOException $e){
		die("connection failed");
		$connect = 'non';
	}
	?>
