var a = [[1, 2, 3], [4, 5, 6], [7, 8, 10]];
var i = 0;
while (i < 3)
{
	console.log(a[i]);
	i = i + 1;
}

a[1] = "4";
a[2][1] = 100;
console.log("\n");
i = 0;
while (i < 3)
{
	console.log(a[i]);
	i = i + 1;
}

