<?php include "./ServerName.php";
  $con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);
  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
  $userID = $_GET['userID'];
  $result = mysqli_query($con, "SELECT temperature_sensor FROM SMARTPOT WHERE userID = '$userID' ");
  $row = mysqli_fetch_array($result);
  $data = $row[0];
  echo $data;
  mysqli_close($con);
?>
