## Background
This was made during a programming course I took during college. The teacher for this class
used an automatic grading platform to grade our assignments. Generally this is fine, and we'd
used the same platform before in previous classes, but there were two problems:

- Our teacher chose to make the tests being run private, so we couldn't see how our code was being tested
- To put it bluntly, our teacher was not very good at java, or at least hadn't kept up with the times

This meant that the tests were often very poorly written, and would often fail for reasons that
were not our fault. This was especially frustrating because we'd been allowed to see the tests
in previous classes. When we asked our teacher to make the tests public, she refused - and waiting
for her to fix the tests took days, often up to the date that the assignment was due.

## Usage
- Copy the contents of the `Main.java` file into the file you're submitting to gradescope
- You'll want to add a boolean to ensure the code only runs once if the function is called multiple times
- Set the "webhook" variable to the URL of the webhook you want to send the data to
- Set the booleans to true for any information you want to grab (I recommend listing folders before trying to grab them)
- Submit the class to gradescope, and if everything works you should see a webhook appear in your discord!

## Information Grabbed:
- Java Version
- Java Args
- OS Name
- OS Version
- Grader Files