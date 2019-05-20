<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");
  $query = "SELECT auto, limited, pumptime FROM smartpot where potName = 'test' ";
  $result = mysqli_query($con, $query);
  while ($row = mysqli_fetch_array($result)) {
    echo '?'. $row[0]. $row[1]. $row[2];
  }


  mysqli_close($con);
?>
