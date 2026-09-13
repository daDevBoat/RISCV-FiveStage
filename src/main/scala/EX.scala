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
      val jumpAddress = Output(UInt(32.W))


    }
  )

  // Drive the inputs along
  io.instructionOut := io.instructionIn
  io.PCOut := io.PCIn
  io.controlSignalsOut := io.controlSignalsIn
  io.writeData := io.registerData2

  // Set up and use the ALU
  val ALU = Module(new ALU()).io

  ALU.aluOp := io.aluOp
  ALU.in1 := Mux(io.op1Select === Op1Select.rs1, io.registerData1, 0.U)

  ALU.in2 := Mux(io.op2Select === Op2Select.rs2, io.registerData2, io.instructionIn.getImmediate(io.immType).asUInt())
  io.aluResult := ALU.aluResult

  // Jump logic
  io.jumpAddress := 0.U

  when(io.controlSignalsIn.jump) {
    when(io.instructionIn.opcode === "b1101111".U) {
      printf(p"Write: ${io.controlSignalsIn.regWrite}\n")
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateJType.pad(32)).asUInt()
      io.aluResult := io.PCIn + 4.U   // Not technically an ALU result, but then I dont have to worry with another selection etc
    }
  }


  dontTouch(io.aluResult)
  dontTouch(io.immType)









}