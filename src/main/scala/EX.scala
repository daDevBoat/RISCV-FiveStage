package FiveStage
import chisel3._
import chisel3.util.{BitPat, MuxCase}
import chisel3.experimental.{MultiIOModule, dontTouch}


class Execute extends MultiIOModule {

  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val PCIn = Input(UInt(32.W))
      val controlSignalsIn = Input(new ControlSignals())
      val branchType = Input(UInt(3.W))
      val op1Select = Input(UInt(1.W))
      val op2Select = Input(UInt(1.W))
      val immType = Input(UInt(3.W))
      val aluOp = Input(UInt(4.W))
      val registerData1 = Input(UInt(32.W))
      val registerData2 = Input(UInt(32.W))

      val instructionOut = Output(new Instruction())
      val PCOut = Output(UInt(32.W))
      val controlSignalsOut = Output(new ControlSignals())
      val aluResult = Output(UInt(32.W))
      val writeData = Output(UInt(32.W))



    }
  )

  // Set up and use the ALU
  val ALU = Module(new ALU()).io

  ALU.aluOp := io.aluOp
  ALU.in1 := Mux(io.op1Select === Op1Select.rs1, io.registerData1, 0.U)

  // Add logic for
  ALU.in2 := Mux(io.op2Select === Op2Select.rs2, io.registerData2, io.instructionIn.getImmediate(io.immType).asUInt())
  io.aluResult := ALU.aluResult


  // Drive the inputs along
  io.instructionOut := io.instructionIn
  io.PCOut := io.PCIn
  io.controlSignalsOut := io.controlSignalsIn
  io.writeData := io.registerData2

  dontTouch(io.aluResult)
  dontTouch(io.immType)









}