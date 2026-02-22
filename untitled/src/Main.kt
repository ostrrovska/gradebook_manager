import java.util.UUID

// h
enum class AssessmentType {
    LAB, TEST, EXAM, PROJECT
}

// j. Extension for base class (String)
fun String.formatAsTitle(): String {
    return this.trim().split(" ").joinToString(" ") {
        it.replaceFirstChar { char -> char.uppercase() }
    }
}

// Base class for users
open class User(val id: String, open val name: String, val email: String?) {

    // e. Method that does not return a value
    open fun displayRole() {
        println("User: $name")
    }
}

// a. Class inheritance
class Student(
    id: String,
    override val name: String,
    email: String?,
    val major: String,
    val year: Int
) : User(id, name, email) {

    private val grades = mutableListOf<Double>()

    // i. Initialization of a Nullable variable
    var advisorName: String? = null

    // f. Static method via companion object
    companion object {
        fun generateStudentId(): String {
            return "STU-${UUID.randomUUID().toString().take(6).uppercase()}"
        }
    }

    // e. Method that returns a value
    fun calculateAverage(): Double {
        return if (grades.isEmpty()) 0.0 else grades.average()
    }

    fun addGrade(grade: Double) {
        grades.add(grade)
    }

    override fun displayRole() {
        // i. Unpacking Nullable variable using the Elvis operator (?:)
        val contactEmail = email ?: "No email provided"
        println("Student: $name, Year: $year, Major: $major | Contact: $contactEmail")

        // i. Unpacking Nullable variable using the scope function 'let'
        advisorName?.let {
            println("Advisor: $it")
        }
    }
}

// Class describing a task (properties and methods)
class CourseTask(
    val title: String,
    val type: AssessmentType,
    val maxScore: Int,
    var isCompleted: Boolean = false // d. Default value in constructor
) {

    var description: String? = null

    // c. Constructor overloading (Secondary constructor)
    constructor(title: String, type: AssessmentType, maxScore: Int, description: String) : this(title, type, maxScore) {
        this.description = description
    }
}

// a. Class inheritance
class Teacher(id: String, override val name: String, email: String?) : User(id, name, email) {

    // b. Method overloading (base grading method)
    fun gradeTask(student: Student, task: CourseTask, score: Double) {
        println("Graded '${task.title}' for ${student.name}: $score/${task.maxScore}")
        student.addGrade(score)
        task.isCompleted = true
    }

    // b. Method overloading (method with an additional parameter - feedback)
    fun gradeTask(student: Student, task: CourseTask, score: Double, feedback: String) {
        gradeTask(student, task, score)
        println("Teacher's feedback: $feedback")
    }
}

// g. Singleton class via 'object'
object GradebookManager {
    private val students = mutableListOf<Student>()

    fun registerStudent(student: Student) {
        students.add(student)
    }

    fun getTopStudents(): List<Student> {
        // k. Writing and using a lambda function
        return students.filter { it.calculateAverage() >= 90.0 }
            .sortedByDescending { it.calculateAverage() }
    }
}

// j. Extension for the 1st custom class
fun Student.isHonorStudent(): Boolean {
    return this.calculateAverage() >= 90.0
}

// j. Extension for the 2nd custom class
fun CourseTask.markAsCritical() {
    this.description = "[CRITICAL] ${this.description ?: "Requires immediate attention"}"
}

fun main() {
    // Demonstrating object creation and method usage
    val studentId = Student.generateStudentId()
    val kat = Student(studentId, "katya ostrovska".formatAsTitle(), "email", "Software Engineering", 3)
    kat.advisorName = "Dr. Smith"

    val teacher = Teacher("T-01", "Prof. Alan Turing", null)

    // Using the secondary constructor
    val lab1 = CourseTask("mobile development lab 1", AssessmentType.LAB, 100, "Intro to OOP")
    lab1.markAsCritical()

    GradebookManager.registerStudent(kat)

    println("--- User Profile ---")
    kat.displayRole()

    println("\n--- Grading Process ---")
    teacher.gradeTask(kat, lab1, 95.0, "Excellent understanding of class architecture!")

    println("\n--- Student Status ---")
    println("Average score: ${kat.calculateAverage()}")
    println("Is Honor Student? ${kat.isHonorStudent()}")
}