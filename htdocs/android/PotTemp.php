<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
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
