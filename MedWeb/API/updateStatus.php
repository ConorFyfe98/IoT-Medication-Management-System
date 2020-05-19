<?php

include_once 'connection.php';

if (isset($_POST['Data'])){
	
//decode json data
$data=$_POST['Data'];
$data = json_decode($data);
$resultsjson = json_decode($data);
$status=$data->status;
$medicationID=$data->medicationID;
		
		//Build sql query
		$query = $conn->prepare("
			        UPDATE medicationData 
					SET status = :status
					WHERE medicationID =:medicationID
        ");

		$success = $query->execute([
			'status' => $status,
			'medicationID' => $medicationID
		]);

		$count = $query->rowCount();

		if ($count > 0) {
			echo"Pill Status Confirmed: Medication ".$medicationID." status = ".$status."";
			
		} else {
			echo "Database was not updated.";			
		}

}else{
	echo "Failed appropriate requirements not met.";
}
?>