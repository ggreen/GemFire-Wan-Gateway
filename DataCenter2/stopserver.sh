# bring down servers in descending order as a discipline for advanced restarting

gfsh <<!

connect --locator=localhost[10335]

shutdown
Y
exit;
!

