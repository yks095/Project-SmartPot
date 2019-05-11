<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $query = "SELECT auto FROM smartpot where potName = 'test' ";
  $result = mysqli_query($con, $query);
  while ($row = mysqli_fetch_array($result)) {
    echo '?'. $row[0];
  }
  mysqli_close($con);
?>
