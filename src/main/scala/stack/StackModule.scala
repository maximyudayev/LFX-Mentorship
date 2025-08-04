package stack

import chisel3.stage.ChiselStage
import chisel3.experimental.ChiselEnum
import java.nio.file.Paths

import chisel3._
import chisel3.util._

// Your code starts here
object Opcode extends ChiselEnum {
  val push  = Value(0x27.U)
  val peek  = Value(0x40.U)
  val pop   = Value(0x43.U)
}

class InstructionBundle extends Bundle {
  val imm = UInt(25.W)
  val opcode = Opcode()
}

class StackModule(val dataWidth: Int, val len: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(new InstructionBundle)
    val out = Output(UInt(dataWidth.W))
    val underflow = Output(Bool())
    val overflow = Output(Bool())
    val isEmpty = Output(Bool())
    val isFull = Output(Bool())
    val popped = Output(Bool())
    val peeked = Output(Bool())
  })

  // Convenience values
  val stackPtrBits = (log2Ceil(len)).W
  val botPtr = 0.U(stackPtrBits)
  val topPtr = len.U(stackPtrBits)
  val emptyEntry = 0.U(dataWidth.W)

  // Intermediate signals for MUXes
  val isWrite = io.in.opcode.isOneOf(Opcode.push)
  val isRead = io.in.opcode.isOneOf(Opcode.pop, Opcode.peek)
  val noneSet = ~(io.in.imm.orR)

  // Registers for stack
  val stack = RegInit(VecInit(Seq.fill(len)(emptyEntry)))
  val stackPtr = RegInit(botPtr)

  // Combinational output flags
  val isEmpty = stackPtr === botPtr
  val isFull = stackPtr === topPtr
  val underflow = isRead & noneSet & isEmpty
  val overflow = isWrite & isFull
  val popped = (io.in.opcode === Opcode.pop) & noneSet & ~isEmpty
  val peeked = (io.in.opcode === Opcode.peek) & noneSet & ~isEmpty

  // Stack output data (output valid data or 0 by default)
  val out = Mux(isRead & noneSet & ~isEmpty, stack(stackPtr), emptyEntry)

  // Increment/decrement pointer
  when (isWrite & ~isFull) {
    stack(stackPtr) := Cat(0.U(7.W), io.in.imm)
    stackPtr := stackPtr + 1.U
  } .elsewhen (popped) {
    stackPtr := stackPtr - 1.U
  }

  // Connect local signals to IO
  io.out := out
  io.underflow := underflow
  io.overflow := overflow
  io.isEmpty := isEmpty
  io.isFull := isFull
  io.popped := popped
  io.peeked := peeked
}
// Your code ends here

object SVGen extends App {
  val out = Paths.get(
    "out",
    this.getClass
      .getName
      .stripSuffix("$")
  ).toString
  new ChiselStage().emitSystemVerilog(
    new StackModule(args(0).toInt, args(1).toInt),
    Array("--target-dir", out),
  )
}

object MyStack extends App {
  new ChiselStage().emitSystemVerilog(
    new StackModule(8, 10),
  )
}
