<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html>

<head>

<meta name="Description" content="Information architecture, Web Design, Web Standards." />
<meta name="Keywords" content="your, keywords" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="Distribution" content="Global" />
<meta name="Author" content="Erwin Aligam - ealigam@gmail.com" />
<meta name="Robots" content="index,follow" />		

<link rel="stylesheet" href="css/main.css" type="text/css" />

<title>ResultCenter</title>
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>

<body>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="header.jsp"></jsp:include>				
	
	<jsp:include page="sidebar.jsp"></jsp:include>	
		
	<div id="main">
			
		<a name="TemplateInfo"></a>
		<h1>Template Info</h1>
				
		<p><strong>Citrus Island 1.1</strong> is is a free, W3C-compliant, CSS-based website template 
		by <strong><a href="http://www.styleshout.com/">styleshout.com</a></strong>. This work is 
		distributed under the <a rel="license" href="http://creativecommons.org/licenses/by/2.5/">
		Creative Commons Attribution 2.5  License</a>, which means that you are free to 
		use and modify it for any purpose. All I ask is that you include a link back to  
		<a href="http://www.styleshout.com/">my website</a> in your credits.</p>  

		<p>For more free designs, you can visit 
		<a href="http://www.styleshout.com/">my website</a> to see 
		my other works.</p>
		
		<p>Good luck and I hope you find my free templates useful!</p>
				
		<p class="post-footer align-right">					
			<a href="index.html" class="readmore">Read more</a>
			<a href="index.html" class="comments">Comments (7)</a>
			<span class="date">Oct 11, 2006</span>	
		</p>
			
		<a name="SampleTags"></a>				
		<h1>Sample Tags</h1>
				
		<h3>Code</h3>				
		<p><code>
		code-sample { <br />
		font-weight: bold;<br />
		font-style: italic;<br />				
		}
		</code></p>	
				
		<h3>Example Lists</h3>
				
		<ol>
			<li>example of</li>
			<li>ordered list</li>								
		</ol>	
							
		<ul>
			<li>example of</li>
			<li>unordered list</li>								
		</ul>				
				
		<h3>Blockquote</h3>			
		<blockquote><p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy 
		nibh euismod tincidunt ut laoreet dolore magna aliquam erat....</p></blockquote>
				
		<h3>Image and text</h3>
		<p><a href="http://getfirefox.com/"><img src="css/images/firefox-gray.jpg" width="100" height="120" alt="firefox" class="float-left" /></a>
		Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Donec libero. Suspendisse bibendum. 
		Cras id urna. Morbi tincidunt, orci ac convallis aliquam, lectus turpis varius lorem, eu 
		posuere nunc justo tempus leo. Donec mattis, purus nec placerat bibendum, dui pede condimentum 
		odio, ac blandit ante orci ut diam. Cras fringilla magna. Phasellus suscipit, leo a pharetra 
		condimentum, lorem tellus eleifend magna, eget fringilla velit magna id neque. Curabitur vel urna. 
		In tristique orci porttitor ipsum. Aliquam ornare diam iaculis nibh. Proin luctus, velit pulvinar 
		ullamcorper nonummy, mauris enim eleifend urna, congue egestas elit lectus eu est. 				
		</p>
								
		<h3>Example Form</h3>
		<form action="#">		
			<p>				
			<label>Name</label>
			<input name="dname" value="Your Name" type="text" size="30" />
			<label>Email</label>
			<input name="demail" value="Your Email" type="text" size="30" />
			<label>Your Comments</label>
			<textarea rows="5" cols="5"></textarea>
			<br />	
			<input class="button" type="submit" />		
			</p>		
		</form>				
		<br />						
							
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
