var a = 1;
var b = 2.7;
var c = 'A';
var d = true;
var e = "compiler";
var f = [1, 'a', [7, 3.4, 'c', 4.8], 3];
while (d == true)
{
	if (f[0] == 1)
	{
		b = b + a;
		a = a + 23;
		f[2][1] = 5;
		f[3] = "pyvascript";
		if (a >= 24)
		{
			a = (a / 2) * 3;
			f[3] = -1;
		}
	}
	else
	{
		b = -1;
		d = false;
	}

}

var g = prompt("statement!!!");
alert(g);
