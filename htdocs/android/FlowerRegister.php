<?php include "./ServerName.php";
  $con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);
  $flower = $_POST["flower"];
  $userID = $_POST["userID"];

  if (mysqli_connect_errno()) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
  $query = "SELECT * FROM FLOWER WHERE flower = '$flower'  ";
  $idx = mysqli_query($con, $query);
  $row=mysqli_fetch_array($idx);
  mysqli_query($con,"UPDATE SMARTPOT SET flower = $row[0] WHERE userID='$userID' ");

  $response = array();
  $response["success"] = true;

  echo json_encode($response);

?>
