<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $query = "SELECT flower FROM smartpot where potName = 'test' ";
  $result = mysqli_query($con, $query);
  $row = mysqli_fetch_array($result);


  if($row[0] != null){
    $query2 = "SELECT pumptime FROM flower where idx = $row[0] ";
    $result2 = mysqli_query($con, $query2);
  }

  while ($row2 = mysqli_fetch_array($result2)) {
    echo "?". $row2[0];
  }

  mysqli_close($con);
?>
