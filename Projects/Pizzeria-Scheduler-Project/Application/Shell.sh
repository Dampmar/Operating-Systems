#!/bin/bash
# filepath: c:\Users\Usuario\Desktop\Repos\pizzeria-scheduler\Application\Shell.sh


# Setup Function
Setup() {
    echo "Setting up development environment..."
    mkdir -p lib bin
    
    if [ ! -f "lib/junit-4.13.2.jar" ]; then
        echo "Downloading JUnit..."
        curl -L "https://search.maven.org/remotecontent?filepath=junit/junit/4.13.2/junit-4.13.2.jar" -o "lib/junit-4.13.2.jar"
    fi
    
    if [ ! -f "lib/hamcrest-core-1.3.jar" ]; then
        echo "Downloading Hamcrest..."
        curl -L "https://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar" -o "lib/hamcrest-core-1.3.jar"
    fi
    
    echo "Setup complete!"
}

# Compile Function
Compile() {
    echo "Compiling Java files..."
    javac -d bin src/lib/*.java src/App.java src/ThreadedApp.java src/components/*.java
    
    if [ $? -ne 0 ]; then
        echo "Compilation failed!"
        return 1
    fi
    
    echo "Compilation successful!"
    return 0
}

# Test Function
UnitTest() {
    echo "Compiling Java Test files..."
    javac -d bin -cp "lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar" src/*.java src/lib/*.java src/components/*.java src/test/*.java
    
    if [ $? -ne 0 ]; then
        echo "Compilation failed!"
        return 1
    fi
    
    echo "Running tests..."
    java -cp "bin:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore \
        test.ChefTest \
        test.DriverTest \
        test.FileParserTest \
        test.OrderTest \
        test.OvenTest \
        test.SchedulerFocusedTest \
        test.SchedulerRoundRobinTest \
        test.SchedulerTest
    
    if [ $? -ne 0 ]; then
        echo "Tests failed!"
        return 1
    fi
    
    echo "All tests passed!"
    return 0
}

# Run Examples
RunExample_One() {
    Compile
    if [ $? -ne 0 ]; then return 1; fi
    
    java -cp bin App --input-file BasicFocused.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy FOCUSED
    return 0
}

RunExample_Two() {
    Compile
    if [ $? -ne 0 ]; then return 1; fi
    
    java -cp bin App --input-file BasicRoundRobin.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy FOCUSED
    return 0
}

RunExample_Three() {
    Compile
    if [ $? -ne 0 ]; then return 1; fi
    
    java -cp bin ThreadedApp --input-files BasicFocused.txt,BasicRoundRobin.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy FOCUSED
    return 0
}

RunExample_Four() {
    Compile
    if [ $? -ne 0 ]; then return 1; fi
    
    java -cp bin App --input-file BasicRoundRobin.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy RR --chef-quantum 2
    return 0
}

RunExample_Five() {
    Compile
    if [ $? -ne 0 ]; then return 1; fi
    
    java -cp bin App --input-file BasicFocused.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy RR --chef-quantum 2
    return 0
}

RunExample_Six() {
    Compile
    if [ $? -ne 0 ]; then return 1; fi
    
    java -cp bin App --input-file JenTest.txt --available-ovens 2 --available-chefs 3 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy RR --chef-quantum 2
    return 0
}

RunExample_Seven() {
    Compile
    if [ $? -ne 0 ]; then return 1; fi
    
    java -cp bin App --input-file JenTest.txt --available-ovens 2 --available-chefs 3 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy FOCUSED
    return 0
}

RunExample_ClassRoundRobin() {
    Compile 
    if [ $? -ne 0 ]; then return 1; fi

    java -cp bin App --input-file ClassTest.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 3 --chef-time 3 --chef-strategy RR --chef-quantum 1
}

RunExample_ClassFocused() {
    Compile 
    if [ $? -ne 0 ]; then return 1; fi

    java -cp bin App --input-file ClassTest.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 3 --chef-time 3 --chef-strategy FOCUSED
}

PrintHelp() {
    echo "Pizza Scheduler Usage:"
    echo "Shell.sh [command]"
    echo ""
    echo "Commands:"
    echo "   setup       Download required dependencies"
    echo "   compile     Compile the project"
    echo "   test        Run unit tests"
    echo "   one         Run simulation 1"
    echo "   two         Run simulation 2"
    echo "   three       Run simulation 3"
    echo "   four        Run simulation 4"
    echo "   five        Run simulation 5"
    echo "   six         Run simulation 6"
    echo "   seven       Run simulation 7"
    echo "   classRR     Run simulation class - ROUND ROBIN"
    echo "   classFoc    Run simulation class - FOCUSED"
    echo ""
    echo "Examples:"
    echo "   ./Shell.sh setup"
    echo "   ./Shell.sh test"
    echo "   ./Shell.sh compile"
    echo "   ./Shell.sh one"
}

# Check if a command is provided
case "$1" in
    setup) Setup ;;
    compile) Compile ;;
    test) UnitTest ;;
    one) RunExample_One ;;
    two) RunExample_Two ;;
    three) RunExample_Three ;;
    four) RunExample_Four ;;
    five) RunExample_Five ;;
    six) RunExample_Six ;;
    seven) RunExample_Seven ;;
    classRR) RunExample_ClassRoundRobin ;;
    classFoc) RunExample_ClassFocused ;;
    *) PrintHelp ;;
esac