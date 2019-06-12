<?php include "./ServerName.php";
  $con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);

  $userID = $_POST["userID"];
  $userEmail = $_POST["userEmail"];
  $potName = $_POST["potName"];


  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

  mysqli_query($con,"UPDATE USER SET userEmail='$userEmail' WHERE userID='$userID' ");
  mysqli_query($con,"UPDATE SMARTPOT SET potName = '$potName' WHERE userID='$userID' ");

  $response = array();
  $response["success"] = true;

  echo json_encode($response);
?>
