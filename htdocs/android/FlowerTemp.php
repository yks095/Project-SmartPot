<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

  $userID = $_GET['userID'];
  $result2 = mysqli_query($con, "SELECT flower FROM SMARTPOT WHERE userID = '$userID' ");
  $row2 = mysqli_fetch_array($result2);

  $result = mysqli_query($con, "SELECT lowTemp, highTemp FROM FLOWER WHERE idx = '$row2[0]' ");
  $row = mysqli_fetch_array($result);

  $data = $row[0];
  $data2 = $row[1];

  // $value = array($data, $data2);

  // echo $value;

  echo $data;
  echo $data2;
  mysqli_close($con);

?>
