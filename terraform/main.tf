terraform {
  required_version = "0.12.9"

  backend "s3" {
  }
}

provider "null" {
  version = "~> 2.1"
}

provider "template" {
  version = "~> 2.1"
}

variable environment {

}


provider "aws" {
  version = "~> 2.7"
  max_retries = 20
}


resource "aws_iam_role" "lambda_s3_role" {
  name = "lambda_s3_role"
  description = "It has the permissions that the function needs to manage objects in Amazon S3 and write logs to CloudWatch Logs"
  assume_role_policy = data.template_file.lambda_s3_role_policy.rendered

  tags = {
    tag-key = "avanzarit-${var.environment}"
  }
}

resource "aws_iam_role_policy_attachment" "aws-lambda-execute-policy-attachment" {
  role = "${aws_iam_role.lambda_s3_role.name}"
  policy_arn = "arn:aws:iam::aws:policy/AWSLambdaExecute"
}

data "template_file" "lambda_s3_role_policy" {
  template = "${file("${path.module}/policies/aws-lambda-execute-role-policy.json")}"
}