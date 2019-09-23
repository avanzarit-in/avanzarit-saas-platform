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

#set AWS_ACCESS_KEY_ID=AKIARMNZ6SY7DEONVDFZ
#set AWS_SECRET_ACCESS_KEY=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
#set AWS_DEFAULT_REGION=us-west-1

provider "aws" {
  version = "~> 2.7"
  access_key = "AKIARMNZ6SY7PIOZDEFH"
  secret_key = "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"
  region = "us-west-1"
  max_retries = 20
}


resource "aws_s3_bucket" "b" {
  bucket = "my-tf-test-bucket-avanzar"
  acl    = "private"

  tags = {
    Name        = "My bucket"
    Environment = "Dev"
  }
}