LFX Mentorship Fall 2025 Coding Challenge
=======================

## Overview

The aim of this coding challenge is to create a hardware stack module which has the ability to push, pop and peek according to a given instruction. In order to attempt the coding challenge, fork this branch of the repository and attempt the challenge. Upon completion, create a PDF file consisting of the link to your fork, the code (with the mention of file name) you have written and the output you recieved.

## Stack Module

The `StackModule` is the name of the hardware stack to be implemented. This main module must be written in `src/main/scala/stack/StackModule.scala`. The `StackModule` must be parameterized with the following parameters.
- **dataWidth:** The bitwidth of a single element to be stored in the stack.
- **len:** The length of the stack.

### Stack Module Interface

#### Inputs

- **io.in:** A 32-bit wide input port. Used to input instructions.

#### Outputs

- **io.out:** A `dataWidth` wide input port. Used to output values from the stack.
- **io.underflow:** A 1-bit port used to indicate underflow flag of the `pop` and `peek` instructions.
- **io.overflow:** A 1-bit port used to indicate overflow flag of the `push` instructions.
- **io.isEmpty:** A 1-bit port used to indicate that the stack is empty.
- **io.isFull:** A 1-bit port used to indicate that the stack is full.
- **io.popped:** A 1-bit port used to indicate that the successful popping of a value from the stack.
- **io.peeked:** A 1-bit port used to indicate that the successful peeking of a value from the stack.

## Instructions

Based on the following instructions and their encodings, the `StackModule` will perform operations.

| instruction | instruction[31:7] (imm[24:0]) | instruction[6:0] (opcode) |
| - | - | - |
| push | imm[24:0] | 0100111 |
| pop | 0000000000000000000000000 | 1000011 |
| peek | 0000000000000000000000000 | 1000000 |

- **push:** The push instruction inserts the `dataWidth`-bit zero-extended immediate to the stack. The `io.isFull` output is asserted when the stack becomes full. If the stack is already full, `io.overflow` will be asserted.
- **pop:** The pop instruction removes the `dataWidth`-bit value from the top of the stack and sends it to `io.out` while also asserting `io.popped`. `io.isEmpty` is asserted if the stack becomes empty. If the stack is already empty, `io.underflow` will be asserted and the value 0 will be read..
- **peek:** The peek instruction reads the `dataWidth`-bit value from the top of the stack and sends it to `io.out` while also asserting `io.peeked`. If the stack is already empty, `io.underflow` will be asserted and the value 0 will be read.

## Installation

The most convenient way to work with the [Chisel language](https://www.chisel-lang.org/) on Windows is via WSL. In case of a managed workstation, running a Linux dual-boot may be not allowed. WSL offers an acceptable workaround.

1. With administrator priveleges in PowerShell, install WSL (will use Ubuntu by default).
   ```ps
   wsl --install
   ``` 

2. Setup user and password for the WSL account when prompted.

3. In the WSL shell, upgrade Linux packages.
   ```sh
   sudo apt update && sudo apt upgrade
   ```

4. Default Python in WSL is externally managed, use virtual environments instead (also cleaner per-project).
   ```sh
   sudo apt install python3-pip python3-venv python3-full
   sudo python3 -m venv <project root folder>/venv-wsl
   ```

5. Installing pip packages now will throw `OSError`. Add metadata configuration to the WSL on mounting the Windows disk.
   ```sh
   echo -e '[automount]\noptions = "metadata"' | sudo tee -a /etc/wsl.conf
   ```
   Exit WSL session.
   ```sh
   exit
   ```
   Terminate the VM from the PowerShell.
   ```ps
   wsl --terminate Ubuntu
   ```
   Reboot the PC.

6. Launch PowerShell and start the `wsl`. You will now be able to install pip packages.

   Activate the environment and install packages required for the given design.
   ```sh
   source ./venv-wsl/bin/activate
   pip install bitarray 'cocotb~=1.9' bitstring
   ```

   (Optional): If a `ModuleNotFoundError` is thrown, mentioning missing `bitarray.util` package, force reinstall `bitarray`:
   ```sh
   pip install --force-reinstall bitarray
   ```

7. Install Scala CLI
   ```sh
   curl -sSLf https://scala-cli.virtuslab.org/get | sh
   ```
   Test that Scala works
   ```sh
   mkdir /home/"$USER"/Downloads && cd "$_"
   curl -O -L https://github.com/chipsalliance/chisel/releases/latest/download/chisel-example.scala
   scala-cli chisel-example.scala
   ```

8. Install the Chisel build environment as a super-user
   ```sh
   # Activate superuser for convenience
   su
   
   # Ensure the necessary packages are present:
   apt install -y wget gpg apt-transport-https
   
   # Download the Eclipse Adoptium GPG key:
   wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor | tee /etc/apt/trusted.gpg.d/adoptium.gpg > /dev/null
   
   # Configure the Eclipse Adoptium apt repository
   echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list
   
   # Update the apt packages
   apt update
   
   # Install
   apt install temurin-17-jdk
   
   # Mill is a modern Scala build tool with simple syntax and a better command-line experience than SBT
   curl -L https://raw.githubusercontent.com/lefou/millw/0.4.11/millw > mill && chmod +x mill
   # You can then move this script to a global install location
   sudo mv mill /usr/local/bin/
   
   # SBT is the more traditional Scala build tool
   curl -s -L https://github.com/sbt/sbt/releases/download/v1.9.7/sbt-1.9.7.tgz | tar xvz
   # Then copy the sbt bootstrap script into a global install location
   sudo mv sbt/bin/sbt /usr/local/bin/
   
   # Verilator is a high-performance, open-source Verilog simulator
   apt install -y verilator
   
   # Exit super-user mode
   exit
   ```

You should now be able to easily develop with Chisel on a Windows machine.

## Run Tests

To simulate and run the tests on your DUT, run:
```sh
source venv-wsl/bin/activate
python3 run_tests.py
```
