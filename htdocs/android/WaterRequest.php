<?php include "./ServerName.php";
  $con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);
  $auto = $_POST["auto"];
  $manual = $_POST["manual"];
  $potCode = $_POST["potCode"];
  $userID = $_POST["userID"];

  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

  mysqli_query($con,"UPDATE SMARTPOT SET auto='$auto', manual = '$manual' WHERE userID='$userID' ");

  $response = array();
  $response["success"] = true;

  echo json_encode($response);
?>
