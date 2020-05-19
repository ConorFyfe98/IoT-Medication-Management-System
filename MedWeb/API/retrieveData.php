<?php
include_once 'connection.php';

//decode parameters
$data=$_POST['Data'];
$data = json_decode($data);
$resultsjson = json_decode($data);
$userID=$data->userID;


$return_arr = array();

//Build sql query to retrieve pending medication for userID
$query = $conn->prepare("select * from medicationData where userID= :userID and status='Pending'");

$success = $query->execute([
			'userID' => $userID
		]);

$count = $query->rowCount();

		if ($count > 0) {
			
			while ($row = $query->fetch()) {
		
				$medicationID = $row['medicationID'];
		$dateTime = $row['dateTime'];
		$pillOneAmount = $row['pillOneAmount'];
		$pillTwoAmount = $row['pillTwoAmount'];
		$pillThreeAmount = $row['pillThreeAmount'];
		$status = $row['status'];

		$return_arr[] = array("medicationID" => $medicationID,
						"dateTime" => $dateTime,
						"pillOneAmount" => $pillOneAmount,
						"pillTwoAmount" => $pillTwoAmount,
						"pillThreeAmount" => $pillThreeAmount,
						"status" => $status);
			}
		
		}else {
			$return_arr[] = array("error" => "No medication data to display");	
		}
		
		echo json_encode($return_arr);
?>